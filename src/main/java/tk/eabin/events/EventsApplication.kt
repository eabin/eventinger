package tk.eabin.events

import com.vaadin.server.SessionInitEvent
import com.vaadin.server.SessionInitListener
import com.vaadin.spring.server.SpringVaadinServlet
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import tk.eabin.events.ui.MyBootstrapListener

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:37
 */
@SpringBootApplication
open class EventsApplication {
    @Bean(name = arrayOf("springBootServletRegistrationBean"))
    open fun servletRegistrationBean(): ServletRegistrationBean {
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
    Database.connect(
            url = "jdbc:h2:tcp://localhost/eventinger",
            driver = "org.h2.Driver",
            user = "sa", password = ""

    )
    SpringApplication.run(EventsApplication::class.java, *args)
}
