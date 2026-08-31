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
import com.ishtaran.sdk.resources.AccountHoldersResource;
import com.ishtaran.sdk.resources.ApiKeysResource;
import com.ishtaran.sdk.resources.ApplicationsResource;
import com.ishtaran.sdk.resources.AssetNetworkCatalogResource;
import com.ishtaran.sdk.resources.AuthResource;
import com.ishtaran.sdk.resources.DepositsResource;
import com.ishtaran.sdk.resources.EnvironmentsResource;
import com.ishtaran.sdk.resources.EventTypesResource;
import com.ishtaran.sdk.resources.EventsResource;
import com.ishtaran.sdk.resources.ExecutionDestinationsResource;
import com.ishtaran.sdk.resources.ExecutionSourcesResource;
import com.ishtaran.sdk.resources.LedgerResource;
import com.ishtaran.sdk.resources.MembersResource;
import com.ishtaran.sdk.resources.NetworkCostPayerAccountsResource;
import com.ishtaran.sdk.resources.NetworkExecutionResource;
import com.ishtaran.sdk.resources.OrganizationsResource;
import com.ishtaran.sdk.resources.PayoutResource;
import com.ishtaran.sdk.resources.RefundsResource;
import com.ishtaran.sdk.resources.SandboxResource;
import com.ishtaran.sdk.resources.SettlementsResource;
import com.ishtaran.sdk.resources.SigningRequestsResource;
import com.ishtaran.sdk.resources.TransactionsResource;
import com.ishtaran.sdk.resources.WebhookDeliveriesResource;
import com.ishtaran.sdk.resources.WalletsResource;
import com.ishtaran.sdk.resources.WebhookEndpointsResource;
import com.ishtaran.sdk.resources.WithdrawalsResource;
import com.ishtaran.sdk.resources.WorkflowsResource;
import com.ishtaran.sdk.webhook.WebhookSignatureVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * The SDK's single public facade -- immutable/thread-safe after {@link #builder()}.{@code build()}
 * (no shared mutable state except {@link BearerTokenHolder}, which is an internal
 * {@code AtomicReference}). Composes Core ({@code resources.*}) and Easy Mode
 * ({@code easy.*}) over the SAME HTTP transport -- Easy Mode never duplicates business logic, it
 * only combines Core calls (see SDK_CAPABILITY_SPEC.md section 5).
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
    private final AccountHoldersResource accountHolders;
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
    private final WalletsResource wallets;
    private final SigningRequestsResource signingRequests;
    private final ExecutionDestinationsResource executionDestinations;
    private final ExecutionSourcesResource executionSources;
    private final NetworkCostPayerAccountsResource networkCostPayerAccounts;
    private final NetworkExecutionResource networkExecution;
    private final PayoutResource payout;

    private IshtaranClient(IshtaranClientConfig config) {
        this(decorateWithLogging(new JdkHttpTransport(config), config), config.apiKey(), config.retryPolicy());
    }

    private static HttpTransport decorateWithLogging(HttpTransport transport, IshtaranClientConfig config) {
        return config.loggingEnabled() ? new com.ishtaran.sdk.http.LoggingTransport(transport) : transport;
    }

    /**
     * Package-private constructor for tests -- lets a fake {@link HttpTransport} be injected
     * (no network) to exercise Easy Mode's composition logic (e.g. {@code receivePayment})
     * end to end, not just resource by resource. Never exposed publicly -- the public API always
     * goes through {@link #builder()}. No retry/API Key (composition tests don't need that --
     * retry and authentication already have their own suites in {@code RetryingTransportTest}/
     * {@code AuthenticatingTransportTest}).
     */
    IshtaranClient(HttpTransport transport) {
        this(transport, null, com.ishtaran.sdk.config.RetryPolicy.disabled());
    }

    private IshtaranClient(HttpTransport rawTransport, String apiKey, com.ishtaran.sdk.config.RetryPolicy retryPolicy) {
        var bearerTokenHolder = new BearerTokenHolder();
        HttpTransport transport = new AuthenticatingTransport(rawTransport, apiKey, bearerTokenHolder);
        transport = new RetryingTransport(transport, retryPolicy);

        // DEC-032 -- dedicated transport for AccountHolder, never the Organization's apiKey nor the
        // Member bearerTokenHolder above: complete domain separation between the two principals,
        // same reasoning as AccountHolderJwtScheme never sharing a key with MemberJwtScheme
        // on the backend.
        var accountHolderTokenHolder = new BearerTokenHolder();
        HttpTransport accountHolderTransport = new AuthenticatingTransport(rawTransport, null, accountHolderTokenHolder);
        accountHolderTransport = new RetryingTransport(accountHolderTransport, retryPolicy);

        this.auth = new AuthResource(transport, bearerTokenHolder);
        this.organizations = new OrganizationsResource(transport);
        this.applications = new ApplicationsResource(transport);
        this.environments = new EnvironmentsResource(transport);
        this.apiKeys = new ApiKeysResource(transport);
        this.members = new MembersResource(transport);
        this.assetNetworkCatalog = new AssetNetworkCatalogResource(transport);
        this.accounts = new AccountsResource(transport);
        this.accountHolders = new AccountHoldersResource(accountHolderTransport, accountHolderTokenHolder);
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
        this.wallets = new WalletsResource(transport);
        this.signingRequests = new SigningRequestsResource(transport);
        this.executionDestinations = new ExecutionDestinationsResource(transport);
        this.executionSources = new ExecutionSourcesResource(transport);
        this.networkCostPayerAccounts = new NetworkCostPayerAccountsResource(transport);
        this.networkExecution = new NetworkExecutionResource(transport);
        this.payout = new PayoutResource(transport);
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

    /** DEC-032 -- self-service, isolated {@code AccountHolder} session (never shares a token with Member/API Key from this same instance). */
    public AccountHoldersResource accountHolders() {
        return accountHolders;
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

    /** SPEC-018/021, checkpoint 7 -- only the extended PUBLIC key travels through this client (INV-SC-01). */
    public WalletsResource wallets() {
        return wallets;
    }

    /** SPEC-019/020/021, checkpoint 7 -- the SDK signs locally ({@link com.ishtaran.sdk.wallet.Signer}) and submits it back. */
    public SigningRequestsResource signingRequests() {
        return signingRequests;
    }

    /** DEC-037 -- a beneficiary's registered on-chain receiving address per AssetNetwork, required before a Settlement can execute under SelfCustody. */
    public ExecutionDestinationsResource executionDestinations() {
        return executionDestinations;
    }

    /** SPEC-ADDRESSPOOL-001, CUSTODY-EXECUTION-MODES.md -- the outbound-only address ExecutionCustody signs FROM to pay network cost, per AssetNetwork. */
    public ExecutionSourcesResource executionSources() {
        return executionSources;
    }

    /** SPEC-NETEXEC-001 -- the Account debited for the charged network cost of a NetworkExecutionQuote. */
    public NetworkCostPayerAccountsResource networkCostPayerAccounts() {
        return networkCostPayerAccounts;
    }

    /** SPEC-NETEXEC-001 -- priced, time-boxed plans for 1..N physical on-chain operations. */
    public NetworkExecutionResource networkExecution() {
        return networkExecution;
    }

    /** SPEC-024/SPEC-025 -- Payable summary and batched Payout execution. */
    public PayoutResource payout() {
        return payout;
    }

    // ---- Easy Mode ----

    /** Direct pass-through to {@code ledger().getBalance()} -- no business transformation (see SDK_CAPABILITY_SPEC.md section 5). */
    public com.ishtaran.sdk.model.dataplane.BalanceResponse getBalance(UUID accountId, UUID assetNetworkId) {
        return ledger.getBalance(accountId, assetNetworkId);
    }

    /**
     * Composes {@code withdrawals().createDestination()} (if needed) + {@code .request()} --
     * never hides the Network Fee, always returns the real {@code withdrawalId} from Core. If
     * {@code existingDestinationId} is null, creates a new destination first.
     */
    public EasyWithdrawResult withdraw(UUID organizationId, UUID environmentId, UUID accountId, UUID assetNetworkId,
                                        BigDecimal amount, String destinationAddress, UUID existingDestinationId) {
        UUID destinationId = existingDestinationId;
        if (destinationId == null) {
            CreateWithdrawalDestinationResult destination =
                    withdrawals.createDestination(organizationId, destinationAddress, assetNetworkId);
            destinationId = destination.withdrawalDestinationId();
        }

        var result = withdrawals.request(organizationId, environmentId, accountId, destinationId, assetNetworkId, amount, null);

        return new EasyWithdrawResult(
                result.withdrawalId(),
                result.amount(),
                result.estimatedNetworkFee(),
                result.estimatedRecipientAmount(),
                result.networkExecutionCost(),
                result.status());
    }

    /** No HTTP call -- local computation (see SDK_CAPABILITY_SPEC.md section 10). */
    public boolean verifyWebhookSignature(String rawBody, String signatureHeader, String timestampHeader, String endpointSecret) {
        return WebhookSignatureVerifier.verify(rawBody, signatureHeader, timestampHeader, endpointSecret);
    }

    /**
     * Composes {@code transactions().create()} + {@code deposits().createPaymentIntent()} + a
     * follow-up GET to obtain the real {@code depositAddress} (only exposed by the dedicated GET,
     * not by the creation POST -- same real behavior documented in
     * {@code examples/quickstart-node/index.js}). {@code payerAccountId}/{@code recipientAccountId}
     * must already exist and be authorized for the Application (a real business rule, not an
     * SDK limitation).
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
     * Safe polling -- never infinite, always with an explicit {@code timeout} and
     * {@code pollInterval} (see SDK_CAPABILITY_SPEC.md section 15). Terminates once the Payment
     * Intent leaves {@code PENDING}/{@code PARTIALLY_PAID} (i.e. {@code PAID}/{@code EXPIRED}/
     * {@code CANCELLED}), or throws {@link TimeoutError} if the deadline runs out first.
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
