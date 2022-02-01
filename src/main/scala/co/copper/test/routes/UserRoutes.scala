package co.copper.test.routes

import co.copper.test.services.{TestJavaService, TestService}
import com.sbuslab.http.RestRoutes
import com.sbuslab.sbus.Context
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.compat.java8.FutureConverters.CompletionStageOps

@Component
@Autowired
class UserRoutes(
  deribitService: TestService,
  testJavaService: TestJavaService,
) extends RestRoutes {

  def anonymousRoutes(implicit context: Context) =
    pathEnd {
      post {
        complete {
          testJavaService.getUsers.toScala
        }
      } ~
      get {
        complete {
          testJavaService.getOk.toScala
        }
      }
    }

}
