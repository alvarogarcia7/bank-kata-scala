package com.example.kata.bank

import akka.actor.{Actor, ActorRef, Props}

class ATM(userIdentification: ActorRef, printer: ActorRef) extends Actor with akka.actor.ActorLogging {

  import com.example.kata.bank.ATM._

  var card: String = _
  var failedPinAttempts: Int = 0

  def receive: Receive = {
    case InsertCard(cardNumber) =>
      this.card = cardNumber
      log.debug(s"$self is now an ATMWithCard")
      context become ATMWithCard
    case SuccessPrinting() => //do nothing
  }

  def ATMWithCard: Receive = {
    case msg@TypePin(pin) =>
      if (isValidPin(pin)) {
        printer ! WelcomeMessage("Hello, John!")
        context become AuthenticatedATM
      } else {
        printer ! WrongPin()
        this.failedPinAttempts += 1
        if (this.failedPinAttempts >= 3) {
          printer ! ErrorMessage("too many failed attempts", msg)
          context become receive
        }
      }
  }

  def AuthenticatedATM: Receive = {
    case Deposit(amount) => printer ! DepositSuccess(amount)
  }

  def isValidPin(pin: String): Boolean = "-".r.split(card)(3) == pin
}

object ATM {
  def props(userIdentification: ActorRef, printer: ActorRef): Props = Props(new ATM(userIdentification, printer))

  case class DepositSuccess(amount: Int)
}
