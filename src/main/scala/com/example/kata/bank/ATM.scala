package com.example.kata.bank

import akka.actor.{Actor, ActorRef, Props}

class ATM(userIdentification: ActorRef) extends Actor {
  var card: String = _
  var loggedIn = false

  def receive = {
    case InsertCard(cardNumber) => {
      this.card = cardNumber
      sender() ! PinRequired()
    }
    case TypePin(pin) =>
      loggedIn = true
      if (isValidPin(pin)) {
        sender() ! WelcomeMessage("Hello, John!")
      } else {
        sender() ! WrongPin()
      }
    case _ if !loggedIn => sender() ! NotLoggedIn()
    case Deposit(amount) => sender() ! SuccessMessage(s"Deposited $amount EUR")
  }

  def isValidPin(pin: String): Boolean = "-".r.split(card)(3) == pin
}

object ATM {
  def props(userIdentification: ActorRef): Props = Props(new ATM(userIdentification))
}
