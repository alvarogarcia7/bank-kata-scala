package com.example.kata.bank

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
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

  implicit val timeout = Timeout(5 seconds)
  val testProbe = TestProbe()
  val atm = system.actorOf(Props[ATM], "atm")

  "Deposit money on an account" should {
    "tells the user the operation was a success" in {
      val deposit = Deposit(500)

      val resultingMessage = atm ? deposit

      val result = Await.result(resultingMessage, timeout.duration)
      result.shouldEqual(SuccessMessage("Deposited 500 EUR"))
    }
  }
}
