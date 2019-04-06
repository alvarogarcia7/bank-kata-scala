package com.example.kata.bank

import akka.actor.{Actor, Props}

class ATM() extends Actor {
  var loggedIn = false

  def receive = {
    case InsertCard(_) => sender() ! PinRequired()
    case TypePin(_) =>
      loggedIn = true
      sender() ! WelcomeMessage("Hello, John!")
    case _ if !loggedIn => sender() ! NotLoggedIn()
    case Deposit(amount) => sender() ! SuccessMessage(s"Deposited $amount EUR")
  }
}

object ATM {
  def props(): Props = Props(new ATM())

  final case class WhoToGreet(who: String)

  case object Greet

}
