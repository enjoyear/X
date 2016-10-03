package com.chen.guo.controllers

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.learn.{Customer, Order}
import play.api.mvc.{Action, Controller}
import com.chen.guo.views.html.index

@Singleton
class HomeController @Inject() extends Controller {
  def indexGet() = Action {
    Ok(index())
  }
}
