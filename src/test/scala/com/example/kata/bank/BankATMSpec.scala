package com.example.kata.bank

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class BankATMSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("BankATM"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  implicit val timeout = Timeout(1 seconds)
  val testProbe = TestProbe()
  val atm = system.actorOf(Props[ATM], "atm")

  "Start the ATM by inserting the card" should {
    "tells the user the card needs a PIN" in {
      val result = atm ? InsertCard("4000-0000-0000-0000")

      blockingGet(result).shouldEqual(PinRequired())
    }
  }

  "Deposit money on an account" should {
    "tells the user the operation was a success" in {
      val resultingMessage = atm ? Deposit(500)

      blockingGet(resultingMessage).shouldEqual(SuccessMessage("Deposited 500 EUR"))
    }
  }

  private def blockingGet(resultingMessage: Future[Any]) = {
    Await.result(resultingMessage, timeout.duration)
  }
}
