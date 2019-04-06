package com.example.kata.bank

trait Printer {
  def print(any: Any)
}

class ScreenPrinter extends Printer {
  override def print(any: Any): Unit = ???
}
