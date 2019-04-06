package com.example.kata.bank

import akka.actor.{Actor, ActorRef, Props}

class ATM(userIdentification: ActorRef, printer: ActorRef) extends Actor {
  var card: String = _
  var loggedIn = false

  def receive = {
    case InsertCard(cardNumber) => {
      this.card = cardNumber
      printer ! PinRequired()
    }
    case TypePin(pin) =>
      loggedIn = true
      if (isValidPin(pin)) {
        printer ! WelcomeMessage("Hello, John!")
      } else {
        printer ! WrongPin()
      }
    case _ if !loggedIn => printer ! NotLoggedIn()
    case Deposit(amount) => printer ! SuccessMessage(s"Deposited $amount EUR")
  }

  def isValidPin(pin: String): Boolean = "-".r.split(card)(3) == pin
}

object ATM {
  def props(userIdentification: ActorRef, printer: ActorRef): Props = Props(new ATM(userIdentification, printer))
}
