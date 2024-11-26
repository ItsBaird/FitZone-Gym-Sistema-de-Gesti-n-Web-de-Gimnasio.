package com.titanboost.gym.titanboostgymproject.config.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.titanboost.gym.titanboostgymproject.models.MembershipPlans;
import com.titanboost.gym.titanboostgymproject.models.Users;
import com.titanboost.gym.titanboostgymproject.models.Users_MembershipsDto;
import com.titanboost.gym.titanboostgymproject.services.MembershipPlansService;
import com.titanboost.gym.titanboostgymproject.services.UsersService;
import com.titanboost.gym.titanboostgymproject.services.Users_MembershipsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Controlador que maneja el proceso de pago mediante PayPal para la compra de membresías.
 * <p>
 * Este controlador gestiona las solicitudes de creación, éxito y cancelación de pagos
 * a través de PayPal, y actualiza la base de datos con los detalles de la membresía
 * una vez que el pago ha sido aprobado.
 * </p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;
    private final MembershipPlansService membershipPlansService;
    private final Users_MembershipsService usersMembershipsService;
    private final UsersService usersService;

    /**
     * Crea un pago en PayPal para un plan de membresía y redirige al usuario a la página
     * de aprobación de PayPal.
     * <p>
     * Este método valida que el plan de membresía exista, obtiene los detalles del usuario
     * autenticado y luego crea un pago en PayPal para dicho plan. Finalmente, redirige
     * al usuario a la URL de aprobación de PayPal.
     * </p>
     *
     * @param planId El ID del plan de membresía que se desea comprar.
     * @return Redirección a la URL de aprobación de PayPal o una página de error en caso de fallo.
     */
    @PostMapping("/payment/create")
    public RedirectView createPayment(@RequestParam("planId") int planId) {
        try {
            // Validar que el plan de membresía existe
            MembershipPlans plan = membershipPlansService.getPlanById(planId)
                    .orElseThrow(() -> new IllegalArgumentException("Plan de membresía no encontrado"));

            // Obtener el usuario autenticado
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = usersService.findByEmail(userEmail);
            if (user == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            // Obtener los detalles del plan para el pago
            BigDecimal price = plan.getPrice();
            String description = plan.getDescription();
            String cancelUrl = "https://localhost:8443/payment/cancel";
            String successUrl = "https://localhost:8443/payment/success?planId=" + planId;

            log.debug("Plan ID: {}", planId);
            log.debug("Price: {}", price);
            log.debug("Description: {}", description);
            log.debug("Cancel URL: {}", cancelUrl);
            log.debug("Success URL: {}", successUrl);

            // Crear el pago en PayPal
            Payment payment = paypalService.createPayment(
                    price.doubleValue(),
                    "USD",
                    "paypal",
                    "sale",
                    "Compra de membresía: " + plan.getName(),
                    cancelUrl,
                    successUrl
            );

            // Redirigir a la URL de aprobación de PayPal
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }

        } catch (PayPalRESTException e) {
            log.error("Error al crear el pago en PayPal: ", e);
        } catch (Exception e) {
            log.error("Error inesperado: ", e);
        }
        return new RedirectView("/payment/error");
    }

    /**
     * Maneja la respuesta de PayPal cuando el pago es aprobado.
     * <p>
     * Este método valida la respuesta de PayPal, verifica si el pago fue aprobado y,
     * en caso afirmativo, crea una nueva membresía para el usuario. Si el pago no
     * fue aprobado, muestra un mensaje de error.
     * </p>
     *
     * @param paymentId El ID del pago recibido de PayPal.
     * @param payerId El ID del pagador recibido de PayPal.
     * @param planId El ID del plan de membresía comprado.
     * @return La vista de éxito o error dependiendo del estado del pago.
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId,
                                 @RequestParam("planId") int planId) {
        try {
            MembershipPlans plan = membershipPlansService.getPlanById(planId)
                    .orElseThrow(() -> new IllegalArgumentException("Plan de membresía no encontrado"));

            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = usersService.findByEmail(userEmail);
            if (user == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            Payment payment = paypalService.executePayment(paymentId, payerId);
            if ("approved".equals(payment.getState())) {
                LocalDateTime purchaseDate = LocalDateTime.now();
                LocalDateTime expirationDate = purchaseDate.plusDays(plan.getDuration());

                Users_MembershipsDto usersMembershipsDto = new Users_MembershipsDto(
                        user.getUser_id(),
                        planId,
                        purchaseDate,
                        expirationDate
                );

                usersMembershipsService.createMembership(usersMembershipsDto);
                return "paymentSuccess";
            } else {
                log.error("Pago no aprobado: {}", payment.getState());
            }

        } catch (PayPalRESTException e) {
            log.error("Error al ejecutar el pago: ", e);
        } catch (Exception e) {
            log.error("Error inesperado: ", e);
        }
        return "paymentError";
    }

    /**
     * Maneja la respuesta de PayPal cuando el pago es cancelado.
     * <p>
     * Este método simplemente muestra una página indicando que el pago fue cancelado.
     * </p>
     *
     * @return La vista que muestra el resultado de la cancelación del pago.
     */
    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "paymentCancel";
    }

    /**
     * Maneja la respuesta de PayPal en caso de un error durante el pago.
     * <p>
     * Este método muestra una página de error si ocurrió un problema durante el proceso de pago.
     * </p>
     *
     * @return La vista que muestra un mensaje de error.
     */
    @GetMapping("/payment/error")
    public String paymentError() {
        return "paymentError";
    }
}
