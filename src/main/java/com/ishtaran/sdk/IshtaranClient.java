package com.ishtaran.sdk;

import com.ishtaran.sdk.auth.BearerTokenHolder;
import com.ishtaran.sdk.config.IshtaranClientConfig;
import com.ishtaran.sdk.easy.EasyPaymentResult;
import com.ishtaran.sdk.easy.EasyWithdrawResult;
import com.ishtaran.sdk.error.TimeoutError;
import com.ishtaran.sdk.http.AuthenticatingTransport;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.http.JdkHttpTransport;
import com.ishtaran.sdk.http.RetryingTransport;
import com.ishtaran.sdk.model.dataplane.CreateWithdrawalDestinationResult;
import com.ishtaran.sdk.model.dataplane.ParticipantInput;
import com.ishtaran.sdk.model.enums.PaymentIntentStatus;
import com.ishtaran.sdk.resources.AccountsResource;
import com.ishtaran.sdk.resources.ApiKeysResource;
import com.ishtaran.sdk.resources.ApplicationsResource;
import com.ishtaran.sdk.resources.AssetNetworkCatalogResource;
import com.ishtaran.sdk.resources.AuthResource;
import com.ishtaran.sdk.resources.DepositsResource;
import com.ishtaran.sdk.resources.EnvironmentsResource;
import com.ishtaran.sdk.resources.EventTypesResource;
import com.ishtaran.sdk.resources.EventsResource;
import com.ishtaran.sdk.resources.LedgerResource;
import com.ishtaran.sdk.resources.MembersResource;
import com.ishtaran.sdk.resources.OrganizationsResource;
import com.ishtaran.sdk.resources.RefundsResource;
import com.ishtaran.sdk.resources.SandboxResource;
import com.ishtaran.sdk.resources.SettlementsResource;
import com.ishtaran.sdk.resources.TransactionsResource;
import com.ishtaran.sdk.resources.WebhookDeliveriesResource;
import com.ishtaran.sdk.resources.WebhookEndpointsResource;
import com.ishtaran.sdk.resources.WithdrawalsResource;
import com.ishtaran.sdk.resources.WorkflowsResource;
import com.ishtaran.sdk.webhook.WebhookSignatureVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Fachada pública única do SDK — imutável/thread-safe após {@link #builder()}.{@code build()}
 * (nenhum estado mutável compartilhado exceto {@link BearerTokenHolder}, que é uma
 * {@code AtomicReference} interna). Compõe Core ({@code resources.*}) e Easy Mode
 * ({@code easy.*}) sobre o MESMO transporte HTTP — Easy Mode nunca duplica lógica de negócio, só
 * combina chamadas Core (ver SDK_CAPABILITY_SPEC.md §5).
 */
public final class IshtaranClient {

    private final AuthResource auth;
    private final OrganizationsResource organizations;
    private final ApplicationsResource applications;
    private final EnvironmentsResource environments;
    private final ApiKeysResource apiKeys;
    private final MembersResource members;
    private final AssetNetworkCatalogResource assetNetworkCatalog;
    private final AccountsResource accounts;
    private final TransactionsResource transactions;
    private final DepositsResource deposits;
    private final LedgerResource ledger;
    private final SettlementsResource settlements;
    private final RefundsResource refunds;
    private final WithdrawalsResource withdrawals;
    private final WorkflowsResource workflows;
    private final EventTypesResource eventTypes;
    private final EventsResource events;
    private final SandboxResource sandbox;
    private final WebhookEndpointsResource webhookEndpoints;
    private final WebhookDeliveriesResource webhookDeliveries;

    private IshtaranClient(IshtaranClientConfig config) {
        this(decorateWithLogging(new JdkHttpTransport(config), config), config.apiKey(), config.retryPolicy());
    }

    private static HttpTransport decorateWithLogging(HttpTransport transport, IshtaranClientConfig config) {
        return config.loggingEnabled() ? new com.ishtaran.sdk.http.LoggingTransport(transport) : transport;
    }

    /**
     * Construtor package-private para testes — permite injetar um {@link HttpTransport} falso
     * (sem rede) e exercitar a lógica de composição do Easy Mode (ex.: {@code receivePayment}) de
     * ponta a ponta, não só resource a resource. Nunca exposto publicamente — a API pública sempre
     * passa por {@link #builder()}. Sem retry/API Key (testes de composição não precisam disso —
     * retry e autenticação já têm suíte própria em {@code RetryingTransportTest}/
     * {@code AuthenticatingTransportTest}).
     */
    IshtaranClient(HttpTransport transport) {
        this(transport, null, com.ishtaran.sdk.config.RetryPolicy.disabled());
    }

    private IshtaranClient(HttpTransport rawTransport, String apiKey, com.ishtaran.sdk.config.RetryPolicy retryPolicy) {
        var bearerTokenHolder = new BearerTokenHolder();
        HttpTransport transport = new AuthenticatingTransport(rawTransport, apiKey, bearerTokenHolder);
        transport = new RetryingTransport(transport, retryPolicy);

        this.auth = new AuthResource(transport, bearerTokenHolder);
        this.organizations = new OrganizationsResource(transport);
        this.applications = new ApplicationsResource(transport);
        this.environments = new EnvironmentsResource(transport);
        this.apiKeys = new ApiKeysResource(transport);
        this.members = new MembersResource(transport);
        this.assetNetworkCatalog = new AssetNetworkCatalogResource(transport);
        this.accounts = new AccountsResource(transport);
        this.transactions = new TransactionsResource(transport);
        this.deposits = new DepositsResource(transport);
        this.ledger = new LedgerResource(transport);
        this.settlements = new SettlementsResource(transport);
        this.refunds = new RefundsResource(transport);
        this.withdrawals = new WithdrawalsResource(transport);
        this.workflows = new WorkflowsResource(transport);
        this.eventTypes = new EventTypesResource(transport);
        this.events = new EventsResource(transport);
        this.sandbox = new SandboxResource(transport);
        this.webhookEndpoints = new WebhookEndpointsResource(transport);
        this.webhookDeliveries = new WebhookDeliveriesResource(transport);
    }

    public static Builder builder() {
        return new Builder();
    }

    // ---- Core ----

    public AuthResource auth() {
        return auth;
    }

    public OrganizationsResource organizations() {
        return organizations;
    }

    public ApplicationsResource applications() {
        return applications;
    }

    public EnvironmentsResource environments() {
        return environments;
    }

    public ApiKeysResource apiKeys() {
        return apiKeys;
    }

    public MembersResource members() {
        return members;
    }

    public AssetNetworkCatalogResource assetNetworkCatalog() {
        return assetNetworkCatalog;
    }

    public AccountsResource accounts() {
        return accounts;
    }

    public TransactionsResource transactions() {
        return transactions;
    }

    public DepositsResource deposits() {
        return deposits;
    }

    public LedgerResource ledger() {
        return ledger;
    }

    public SettlementsResource settlements() {
        return settlements;
    }

    public RefundsResource refunds() {
        return refunds;
    }

    public WithdrawalsResource withdrawals() {
        return withdrawals;
    }

    public WorkflowsResource workflows() {
        return workflows;
    }

    public EventTypesResource eventTypes() {
        return eventTypes;
    }

    public EventsResource events() {
        return events;
    }

    public SandboxResource sandbox() {
        return sandbox;
    }

    public WebhookEndpointsResource webhookEndpoints() {
        return webhookEndpoints;
    }

    public WebhookDeliveriesResource webhookDeliveries() {
        return webhookDeliveries;
    }

    // ---- Easy Mode ----

    /** Passagem direta para {@code ledger().getBalance()} — sem transformação de negócio (ver SDK_CAPABILITY_SPEC.md §5). */
    public com.ishtaran.sdk.model.dataplane.BalanceResponse getBalance(UUID accountId, UUID assetNetworkId) {
        return ledger.getBalance(accountId, assetNetworkId);
    }

    /**
     * Compõe {@code withdrawals().createDestination()} (se necessário) + {@code .request()} —
     * nunca esconde a Network Fee, sempre devolve o {@code withdrawalId} real do Core. Se
     * {@code existingDestinationId} for nulo, cria um destino novo primeiro.
     */
    public EasyWithdrawResult withdraw(UUID organizationId, UUID accountId, UUID assetNetworkId,
                                        BigDecimal amount, String destinationAddress, UUID existingDestinationId) {
        UUID destinationId = existingDestinationId;
        if (destinationId == null) {
            CreateWithdrawalDestinationResult destination =
                    withdrawals.createDestination(organizationId, destinationAddress, assetNetworkId);
            destinationId = destination.withdrawalDestinationId();
        }

        var result = withdrawals.request(organizationId, accountId, destinationId, assetNetworkId, amount, null);

        return new EasyWithdrawResult(
                result.withdrawalId(),
                result.amount(),
                result.estimatedNetworkFee(),
                result.estimatedRecipientAmount(),
                result.status());
    }

    /** Sem chamada HTTP — cálculo local (ver SDK_CAPABILITY_SPEC.md §10). */
    public boolean verifyWebhookSignature(String rawBody, String signatureHeader, String timestampHeader, String endpointSecret) {
        return WebhookSignatureVerifier.verify(rawBody, signatureHeader, timestampHeader, endpointSecret);
    }

    /**
     * Compõe {@code transactions().create()} + {@code deposits().createPaymentIntent()} + um GET
     * de acompanhamento para obter o {@code depositAddress} real (só exposto pelo GET dedicado, não
     * pelo POST de criação — mesmo comportamento real documentado em
     * {@code examples/quickstart-node/index.js}). {@code payerAccountId}/{@code recipientAccountId}
     * devem já existir e estar autorizados para a Application (regra de negócio real, não uma
     * limitação do SDK).
     */
    public EasyPaymentResult receivePayment(UUID organizationId, UUID applicationId, UUID payerAccountId,
                                             UUID recipientAccountId, UUID assetNetworkId, BigDecimal amount) {
        var participants = List.of(
                new ParticipantInput(payerAccountId, "payer", true, null),
                new ParticipantInput(recipientAccountId, "recipient", false, null));
        var createdTransaction = transactions.create(organizationId, applicationId, null, assetNetworkId, amount, participants, null);

        var createdPaymentIntent = deposits.createPaymentIntent(
                organizationId, createdTransaction.transactionId(), assetNetworkId, amount, null, null);

        return getPayment(createdTransaction.transactionId(), createdPaymentIntent.paymentIntentId());
    }

    public EasyPaymentResult getPayment(UUID transactionId, UUID paymentIntentId) {
        var transaction = transactions.get(transactionId);
        var paymentIntent = deposits.getPaymentIntent(paymentIntentId);
        return new EasyPaymentResult(
                transactionId, paymentIntentId, transaction.status(), paymentIntent.status(),
                paymentIntent.depositAddress(), transaction.amount());
    }

    /**
     * Polling seguro — nunca infinito, sempre com {@code timeout} e {@code pollInterval}
     * explícitos (ver SDK_CAPABILITY_SPEC.md §15). Termina quando o Payment Intent sai de
     * {@code PENDING}/{@code PARTIALLY_PAID} (ou seja, {@code PAID}/{@code EXPIRED}/
     * {@code CANCELLED}), ou lança {@link TimeoutError} se o prazo se esgotar antes.
     */
    public EasyPaymentResult waitForPayment(UUID transactionId, UUID paymentIntentId, Duration timeout, Duration pollInterval) {
        return com.ishtaran.sdk.util.Polling.until(
                () -> getPayment(transactionId, paymentIntentId),
                result -> result.paymentIntentStatus() != PaymentIntentStatus.PENDING
                        && result.paymentIntentStatus() != PaymentIntentStatus.PARTIALLY_PAID,
                timeout, pollInterval, "paymentIntentId=" + paymentIntentId);
    }

    public static final class Builder {
        private final IshtaranClientConfig.Builder configBuilder = IshtaranClientConfig.builder();

        public Builder apiKey(String apiKey) {
            configBuilder.apiKey(apiKey);
            return this;
        }

        public Builder environment(com.ishtaran.sdk.config.Environment environment) {
            configBuilder.environment(environment);
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            configBuilder.baseUrl(baseUrl);
            return this;
        }

        public Builder connectTimeout(Duration timeout) {
            configBuilder.connectTimeout(timeout);
            return this;
        }

        public Builder requestTimeout(Duration timeout) {
            configBuilder.requestTimeout(timeout);
            return this;
        }

        public IshtaranClient build() {
            return new IshtaranClient(configBuilder.build());
        }
    }
}
