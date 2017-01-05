package tk.eabin.events

import com.vaadin.server.SessionInitEvent
import com.vaadin.server.SessionInitListener
import com.vaadin.spring.server.SpringVaadinServlet
import org.jetbrains.exposed.sql.Database
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import tk.eabin.events.db.dummy.createDummyData
import tk.eabin.events.event.notification.Notifier
import tk.eabin.events.event.notification.PushoverNotifier
import tk.eabin.events.ui.MyBootstrapListener
import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:37
 */
@SpringBootApplication
open class EventsApplication {
    @Value("\${db.createDummy}")
    var createDummy = false

    @Value("\${eventinger.pushover.api-token}")
    var pushoverApiKey = ""


    @Autowired
    var dataSource: DataSource? = null

    val notifiers = mutableSetOf<Notifier>()

    @Bean(name = arrayOf("springBootServletRegistrationBean"))
    open fun servletRegistrationBean(): ServletRegistrationBean {
        // todo: could make use of spring data source
        Database.connect(dataSource ?: throw IllegalStateException("No datasource configured in application.properties"))
        if (createDummy) {
            createDummyData()
        }

        if (pushoverApiKey.isNotEmpty()) {
            println("Initializing pushover notifier...")
            notifiers += PushoverNotifier(pushoverApiKey)
        }

//        Server.createWebServer("-webPort", "9091").start()

        val servlet = object : SpringVaadinServlet() {


            public override fun servletInitialized() {
                super.servletInitialized()
                service.addSessionInitListener(object : SessionInitListener {

                    override fun sessionInit(event: SessionInitEvent) {
                        // required for response re-layouting:
                        event.session.addBootstrapListener(MyBootstrapListener())
                    }

                })
            }

        }

        return ServletRegistrationBean(servlet, "/*")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(EventsApplication::class.java, *args)
}
