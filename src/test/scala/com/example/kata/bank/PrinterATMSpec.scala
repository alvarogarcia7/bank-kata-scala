package com.example.kata.bank

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{TestKit, _}
import akka.util.Timeout
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class PrinterATMSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll
    with BeforeAndAfter {

  var actor: ActorRef = _
  var printer: Printer = _

  before {
    printer = mock(classOf[Printer])
    actor = system.actorOf(PrinterActor.props(printer))
  }

  def this() = this(ActorSystem("BankATM"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  implicit val timeout: Timeout = Timeout(3.seconds.dilated)


  "Print the messages" should {
    "PinRequired" in {

      val result = actor ? PinRequired()

      waitForSuccessPrinting(result)
      verify(printer).print("The pin is required")
    }

    "NotLoggedIn" in {

      val result = actor ? NotLoggedIn()

      waitForSuccessPrinting(result)
      verify(printer).print("You need to log in for that")
    }

    "WrongPin" in {

      val result = actor ? WrongPin()

      waitForSuccessPrinting(result)
      verify(printer).print("This pin is not correct")
    }

    "WelcomeMessage" in {

      val result = actor ? WelcomeMessage("x")

      waitForSuccessPrinting(result)
      verify(printer).print("Please, be welcome. x")
    }

    "SuccessMessage" in {

      val result = actor ? SuccessMessage("x")

      waitForSuccessPrinting(result)
      verify(printer).print("Success: x")
    }

    "ErrorMessage" in {

      val result = actor ? ErrorMessage("x", null)

      waitForSuccessPrinting(result)
      verify(printer).print("Error: x")
    }
  }

  private def waitForSuccessPrinting(result: Future[Any]) = {
    blockingGet(result).shouldEqual(SuccessPrinting())
  }

  private def blockingGet(resultingMessage: Future[Any]) = {
    Await.result(resultingMessage, timeout.duration)
  }

}
