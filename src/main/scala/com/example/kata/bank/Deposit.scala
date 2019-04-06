package com.example.kata.bank;


case class SuccessMessage(message: String)
case class WelcomeMessage(message: String)
case class PinRequired()
case class NotLoggedIn()

case class Deposit(amount: Int)
case class InsertCard(cardNumber: String)
case class TypePin(pin: String)
