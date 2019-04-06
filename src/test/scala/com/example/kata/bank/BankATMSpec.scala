package com.example.kata.bank

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout
import org.mockito.Mockito

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class BankATMSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll
    with BeforeAndAfter {

  var testProbe: TestProbe = _
  var atm: ActorRef = _
  var printer: TestProbe = _

  before {
    testProbe = TestProbe()
    //    printer = Mockito.mock(classOf[Printer])
    printer = TestProbe()
    atm = system.actorOf(ATM.props(testProbe.ref, printer.ref))
  }

  def this() = this(ActorSystem("BankATM"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  implicit val timeout: Timeout = Timeout(1 seconds)


  "Start the ATM by inserting the card" should {
    "tells the user the card needs a PIN" in {
      atm ! InsertCard("4000-0000-0000-0000")

      val msg = printer.fishForSpecificMessage() {
        case msg@PinRequired() ⇒ msg
      }
      msg should be(PinRequired())
    }

    "Welcomes the user after the pin is inserted" in {
      atm ! InsertCard("4000-0000-0000-0123")

      atm ! TypePin("0123")

      val msg = printer.fishForSpecificMessage() {
        case msg@WelcomeMessage(_) ⇒ msg
      }
      msg should be(WelcomeMessage("Hello, John!"))
    }

    "Says wrong ping after the wrong pin is inserted" in {
      atm ! InsertCard("4000-0000-0000-0123")

      atm ! TypePin("0000")

      val msg = printer.fishForSpecificMessage() {
        case msg@WrongPin() ⇒ msg
      }
      msg should be(WrongPin())
    }
  }

  "No operation can be performed without the card" should {
    "deposit" in {
      atm ! Deposit(500)

      val msg = printer.fishForSpecificMessage() {
        case msg@NotLoggedIn() ⇒ msg
      }
      msg should be(NotLoggedIn())
    }
  }

  "Deposit money on an account" should {
    "tells the user the operation was a success" in {
      atm ! InsertCard("4000-0000-0000-0123")
      atm ! TypePin("0123")

      atm ! Deposit(500)

      val msg = printer.fishForSpecificMessage() {
        case msg@SuccessMessage(_) ⇒ msg
      }
      msg should be(SuccessMessage("Deposited 500 EUR"))
    }
  }

  private def blockingGet(resultingMessage: Future[Any]) = {
    Await.result(resultingMessage, timeout.duration)
  }
}
