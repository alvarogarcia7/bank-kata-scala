package com.example.kata.bank

import akka.actor.{Actor, ActorSystem, Props}

object ATM {
  def props(): Props = Props(new ATM())
  final case class WhoToGreet(who: String)
  case object Greet
}

class ATM() extends Actor {

  def receive = {
    case Deposit(amount) => sender() ! SuccessMessage("Deposited 500 EUR")
    case InsertCard(_) => sender() ! PinRequired()
    case TypePin(_) => sender() ! WelcomeMessage("Hello, John!")
  }
}

object AkkaQuickstart extends App {

  // Create the 'helloAkka' actor system
  val system: ActorSystem = ActorSystem("helloAkka")

  // Execute the Main here
}
