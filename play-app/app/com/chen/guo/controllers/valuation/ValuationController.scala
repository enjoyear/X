package com.chen.guo.controllers.valuation

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.valuation.AnalyzeRequest
import com.chen.guo.util.fetcher.HistoricalDataFetcher
import com.chen.guo.views.html.valuation.valuation
import java.lang.Double
import java.util
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
      Ok(valuation(requestForm, AnalyzeRequest.emptyRequest, new util.TreeMap[Integer, Double]()))
    else {
      val data: util.TreeMap[Integer, Double] = HistoricalDataFetcher.getSingle(codeOrName).get
      Ok(valuation(requestForm, AnalyzeRequest(codeOrName), data))
    }
  }

  def formPost = Action { implicit request =>
    requestForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(valuation(formWithErrors, AnalyzeRequest.emptyRequest, new util.TreeMap[Integer, Double]()))
      },
      requestFormData => {
        Redirect(routes.ValuationController.formGet(requestFormData.name))
      }
    )
  }
}
