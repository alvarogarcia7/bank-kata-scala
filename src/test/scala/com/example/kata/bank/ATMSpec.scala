package com.example.kata.bank

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import akka.util.Timeout
import com.example.kata.bank.ATM.DepositSuccess
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class ATMSpec(_system: ActorSystem)
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
    printer = TestProbe()
    atm = system.actorOf(ATM.props(testProbe.ref, printer.ref))
  }

  def this() = this(ActorSystem("BankATM"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  implicit val timeout: Timeout = Timeout(1 seconds)


  "Start the ATM by inserting the card" should {
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

    "The user can log in after one wrong PIN" in {
      atm ! InsertCard("4000-0000-0000-0123")
      atm ! TypePin("0000")

      atm ! TypePin("0123")

      val msg = printer.fishForSpecificMessage() {
        case msg@WelcomeMessage(_) ⇒ msg
      }
      msg should be(WelcomeMessage("Hello, John!"))
    }

    "The user can log in after two wrong PIN" in {
      atm ! InsertCard("4000-0000-0000-0123")
      atm ! TypePin("0000")
      atm ! TypePin("0000")

      atm ! TypePin("0123")

      val msg = printer.fishForSpecificMessage() {
        case msg@WelcomeMessage(_) ⇒ msg
      }
      msg should be(WelcomeMessage("Hello, John!"))
    }

    "The user can not log in after three wrong PIN attempt" in {
      atm ! InsertCard("4000-0000-0000-0123")
      atm ! TypePin("0000")
      atm ! TypePin("0000")
      atm ! TypePin("0000")

      atm ! TypePin("0123")

      //TODO AGB how to test that the message is not received?
    }

    "The user can not log in again after failing" in {
      atm ! InsertCard("4000-0000-0000-0123")
      atm ! TypePin("0000")
      atm ! TypePin("0000")
      atm ! TypePin("0000")

      atm ! InsertCard("4000-0000-0000-0123")

      atm ! TypePin("0123")

      val msg = printer.fishForSpecificMessage() {
        case msg@WelcomeMessage(_) ⇒ msg
      }
      msg should be(WelcomeMessage("Hello, John!"))
    }
  }

  "Deposit money on an account" should {
    "tells the user the operation was a success" in {
      atm ! InsertCard("4000-0000-0000-0123")
      atm ! TypePin("0123")

      atm ! Deposit(500)

      val msg = printer.fishForSpecificMessage() {
        case msg@DepositSuccess(_) ⇒ msg
      }
      msg should be(DepositSuccess(500))
    }
  }
}
