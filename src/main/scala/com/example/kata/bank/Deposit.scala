package com.example.kata.bank;


case class SuccessMessage(message: String)
case class PinRequired()

case class Deposit(amount: Int)
case class InsertCard(cardNumber: String)
