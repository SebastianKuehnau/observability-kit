package com.example.application.views.helloworld;

import com.example.application.views.MainLayout;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HelloWorldView extends HorizontalLayout {

    private final TextField name;

    public HelloWorldView() {
        name = new TextField("Your name");
        Button sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> Notification.show("Hello " + name.getValue()));
        sayHello.addClickShortcut(Key.ENTER);

        Button crashButton = new Button("Crash");
        crashButton.addClickListener(buttonClickEvent -> {
            throw new RuntimeException("This is a test exception");
        });

        Button longTask = new Button("Long running task");
        longTask.addClickListener(e -> {
            startLongTask();
            Notification.show("Job completed for " + name.getValue());
        });

        Button spanExample = new Button("Custom span example");
        spanExample.addClickListener(e -> spanExample(e.getClientX(), e.getClientY()));

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello, crashButton, longTask,
            spanExample);

        add(name, sayHello, crashButton, longTask, spanExample);
    }

    @WithSpan
    private void startLongTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void spanExample(int clientX, int clientY) {
        Tracer tracer = GlobalOpenTelemetry.getTracer(
            "observability-kit-demo",
            "1.0");
        final Span span = tracer.spanBuilder("Span Example").startSpan();
        try {
            span.setAttribute("client.x", clientX);
            span.setAttribute("client.y", clientY);
            span.setAttribute("error-message", "This could be your custom error message");
        } finally {
            span.end();
        }
    }
}
