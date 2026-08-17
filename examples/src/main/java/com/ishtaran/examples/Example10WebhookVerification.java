package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.webhook.WebhookSignatureVerifier;

import java.time.Instant;

/**
 * 10 — Verificação de assinatura de webhook. Único exemplo 100% executável sem uma API real
 * rodando (cálculo local, sem chamada HTTP) — simula uma entrega real da plataforma para
 * demonstrar o protocolo completo, incluindo o caso de payload adulterado sendo rejeitado.
 */
public final class Example10WebhookVerification {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey("example-key-not-a-real-network-call")
                .environment(Environment.LOCAL)
                .build();

        String endpointSecret = "whsec_example_secret_do_not_use_in_production";
        String rawBody = "{\"eventType\":\"payment.received\",\"amount\":100}";
        long timestamp = Instant.now().getEpochSecond();

        // Do lado da plataforma: assinatura calculada e enviada nos headers X-Webhook-Signature/
        // X-Webhook-Timestamp junto com o rawBody como corpo da entrega HTTP real.
        String signature = WebhookSignatureVerifier.compute(timestamp, rawBody, endpointSecret);
        System.out.println("Assinatura calculada (simulando a plataforma): " + signature);

        // Do lado do integrador: verificação real usando o SDK, sem chamada de rede.
        boolean valid = client.verifyWebhookSignature(rawBody, signature, String.valueOf(timestamp), endpointSecret);
        System.out.println("Assinatura válida? " + valid);

        // Payload adulterado depois do envio -- a verificação deve rejeitar.
        String tamperedBody = "{\"eventType\":\"payment.received\",\"amount\":999999}";
        boolean tamperedValid = client.verifyWebhookSignature(tamperedBody, signature, String.valueOf(timestamp), endpointSecret);
        System.out.println("Payload adulterado ainda válido? " + tamperedValid + " (esperado: false)");
    }
}
