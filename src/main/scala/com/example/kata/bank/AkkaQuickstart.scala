package com.example.kata.bank

import akka.actor.{Actor, ActorSystem, Props}

object ATM {
  def props(): Props = Props(new ATM())

  final case class WhoToGreet(who: String)

  case object Greet

}

class ATM() extends Actor {
  var loggedIn = false

  def receive = {
    case Deposit(amount) => {
      if (loggedIn) {
        sender() ! SuccessMessage("Deposited 500 EUR")
      } else {
        sender() ! NotLoggedIn()
      }
    }
    case InsertCard(_) => sender() ! PinRequired()
    case TypePin(_) => {
      loggedIn = true
      sender() ! WelcomeMessage("Hello, John!")
    }
  }
}

object AkkaQuickstart extends App {

  // Create the 'helloAkka' actor system
  val system: ActorSystem = ActorSystem("helloAkka")

  // Execute the Main here
}
