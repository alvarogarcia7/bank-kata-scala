package com.example.kata.bank

import akka.actor.{Actor, ActorRef, Props}

class ATM(userIdentification: ActorRef, printer: ActorRef) extends Actor with akka.actor.ActorLogging {

  import com.example.kata.bank.ATM._

  def receive: Receive = {
    case InsertCard(cardNumber) =>
      log.debug(s"$self is now an ATMWithCard")
      context become ATMWithCard(cardNumber, 0)
    case SuccessPrinting() => //do nothing
  }

  def ATMWithCard(cardNumber: String, failedAttempts: Int): Receive = {
    case TypePin(pin) =>
      if (isValidPin(pin, cardNumber)) {
        printer ! WelcomeMessage("Hello, John!")
        context become AuthenticatedATM
      } else {
        if (failedAttempts >= 3) {
          context become receive
        } else {
          context become ATMWithCard(cardNumber, failedAttempts + 1)
        }
        printer ! WrongPin()
      }
  }

  def AuthenticatedATM: Receive = {
    case Deposit(amount) => printer ! DepositSuccess(amount)
  }

  def isValidPin(pin: String, card: String): Boolean = "-".r.split(card)(3) == pin
}

object ATM {
  def props(userIdentification: ActorRef, printer: ActorRef): Props = Props(new ATM(userIdentification, printer))

  case class DepositSuccess(amount: Int)

}
