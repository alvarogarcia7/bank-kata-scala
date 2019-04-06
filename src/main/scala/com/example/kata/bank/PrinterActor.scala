package com.example.kata.bank

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class PrinterActor(printer: Printer) extends Actor {
  def receive = {
    case PinRequired() => printer.print("The pin is required")
      sender() ! SuccessPrinting()
    case NotLoggedIn() => printer.print("You need to log in for that")
      sender() ! SuccessPrinting()
    case WrongPin() => printer.print("This pin is not correct")
      sender() ! SuccessPrinting()
    case WelcomeMessage(message) => printer.print(s"Please, be welcome. $message")
      sender() ! SuccessPrinting()
    case SuccessMessage(message) => printer.print(s"Success: $message")
      sender() ! SuccessPrinting()
  }
}

object PrinterActor {
  def props(printer: Printer): Props = Props(new PrinterActor(printer))
}

case class SuccessPrinting()
