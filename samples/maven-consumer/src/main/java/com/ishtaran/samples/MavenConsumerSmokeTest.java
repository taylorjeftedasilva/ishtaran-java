package com.ishtaran.samples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.webhook.WebhookSignatureVerifier;

/**
 * Prova de consumo real do artefato {@code com.ishtaran:ishtaran-java} publicado no .m2 local —
 * "package dry run" exigido pelo brief do SDK Program antes de considerar o Java SDK pronto para
 * empacotamento. Não faz chamada de rede real (não há Sandbox/Production real disponível ainda,
 * ver SDK_CAPABILITY_SPEC.md §2) — só prova que o classpath resolve e a API pública compila e roda.
 */
public final class MavenConsumerSmokeTest {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .environment(Environment.LOCAL)
                .apiKey("test-key")
                .build();

        if (client.withdrawals() == null || client.accounts() == null || client.auth() == null) {
            throw new IllegalStateException("Client mal construído");
        }

        boolean verified = WebhookSignatureVerifier.verify(
                "{}", "deadbeef", String.valueOf(System.currentTimeMillis() / 1000), "secret");
        if (verified) {
            throw new IllegalStateException("Assinatura inválida não deveria verificar");
        }

        System.out.println("MAVEN_CONSUMER_SMOKE_TEST: OK — ishtaran-java consumido com sucesso via Maven local.");
    }
}
