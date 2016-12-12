package com.chen.guo.controllers

import javax.inject.{Inject, Singleton}

import com.chen.guo.views.html.index
import play.api.mvc.{Action, Controller}

@Singleton
class HomeController @Inject() extends Controller {
  def indexGet() = Action {
    Ok(index())
  }
}
