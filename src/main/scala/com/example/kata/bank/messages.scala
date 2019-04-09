package com.example.kata.bank;


case class SuccessMessage(message: String)
case class WelcomeMessage(message: String)
case class ErrorMessage(message: String, payload: Any)
case class PinRequired()
case class NotLoggedIn()
case class WrongPin()
