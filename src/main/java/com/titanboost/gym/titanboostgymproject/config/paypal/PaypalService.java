package com.titanboost.gym.titanboostgymproject.config.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Servicio que interactúa con la API de PayPal para crear y ejecutar pagos.
 * <p>
 * Este servicio proporciona métodos para crear pagos, configurar las transacciones,
 * y ejecutar pagos a través de la integración con PayPal utilizando la API REST.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PaypalService {

    private final APIContext apiContext;

    /**
     * Crea un pago a través de PayPal utilizando los parámetros especificados.
     * <p>
     * Este método configura el pago con la información proporcionada, como el total
     * a pagar, la moneda, el método de pago, la descripción y las URLs de cancelación
     * y éxito. Después de configurar el pago, se devuelve el objeto `Payment` que
     * se usará para redirigir al usuario a la página de aprobación de PayPal.
     * </p>
     *
     * @param total El total a pagar en el pago.
     * @param currency La moneda en la que se realiza el pago (por ejemplo, "USD").
     * @param method El método de pago, que generalmente es "paypal".
     * @param intent El intento del pago, que puede ser "sale", "authorize" o "order".
     * @param description La descripción del pago, que generalmente describe el artículo o servicio.
     * @param cancelUrl La URL a la que el usuario será redirigido si cancela el pago.
     * @param successUrl La URL a la que el usuario será redirigido si el pago se completa con éxito.
     * @return El objeto `Payment` creado, que contiene la información del pago y las URLs de redirección.
     * @throws PayPalRESTException Si ocurre un error en la creación del pago.
     */
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total)); // 9.99$ - 9.99eur

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);

    }


    /**
     * Ejecuta un pago previamente creado con PayPal.
     * <p>
     * Este método toma el `paymentId` y el `payerId` del pago que ha sido aprobado por el usuario,
     * y completa la ejecución del pago a través de la API de PayPal.
     * </p>
     *
     * @param paymentId El ID del pago que fue creado previamente.
     * @param payerId El ID del pagador recibido de PayPal, que es necesario para completar el pago.
     * @return El objeto `Payment` que representa el pago ejecutado, con detalles como el estado del pago.
     * @throws PayPalRESTException Si ocurre un error durante la ejecución del pago.
     */
    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId((paymentId));

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
}
