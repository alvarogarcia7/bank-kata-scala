package com.example.kata.bank

import akka.actor.ActorSystem

object AkkaQuickstart extends App {

  val system: ActorSystem = ActorSystem("helloAkka")

  // Execute the Main here

  val printer = new ScreenPrinter()
  val userIdentification = system.actorOf(PrinterActor.props(printer), "userIdentification")
  val printerActor = system.actorOf(PrinterActor.props(printer), "printer")
  val atm = system.actorOf(ATM.props(userIdentification, printerActor), "ATM")


  atm ! InsertCard("4000-0000-0000-000")
  atm ! TypePin("0000")
  atm ! TypePin("0000")
  atm ! TypePin("0000")
  atm ! TypePin("0123")
}
