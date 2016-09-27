package com.chen.guo.controllers

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.{Customer, Order}
import play.api.mvc.{Action, Controller}

@Singleton
class ExampleCustomerOrder @Inject() extends Controller {

  def showCustomerOrders = Action {
    Ok(com.chen.guo.views.html.learn.plaintext(Customer("Chen"), List(Order("Ord-1"), Order("Ord-2"))))
  }
}
