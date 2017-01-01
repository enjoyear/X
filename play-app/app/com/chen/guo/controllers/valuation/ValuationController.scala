package com.chen.guo.controllers.valuation

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.valuation.AnalyzeRequest
import com.chen.guo.views.html.valuation.valuation
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.mvc.{Action, Controller}
import play.api.i18n.{I18nSupport, MessagesApi}

@Singleton
class ValuationController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val requestForm = Form(
    mapping(
      "name" -> text
    )(AnalyzeRequest.apply)(AnalyzeRequest.unapply)
  )

  def formGet(codeOrName: String) = Action {
    if (codeOrName.trim.isEmpty)
      Ok(valuation(requestForm, AnalyzeRequest.emptyRequest))
    else
      Ok(valuation(requestForm, AnalyzeRequest(codeOrName)))
  }

  def formPost = Action { implicit request =>
    requestForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(valuation(formWithErrors, AnalyzeRequest.emptyRequest))
      },
      requestFormData => {
        Redirect(routes.ValuationController.formGet(requestFormData.name))
      }
    )
  }
}
