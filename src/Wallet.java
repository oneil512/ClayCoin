public class Wallet {
    private Integer balance;

    private String address;
    private String privateKey;

    public Wallet(){
        this.privateKey = randomAlphaNumeric(32);
        this.address = randomAlphaNumeric(32);
    }

    public Integer getBalance() {
        return balance;
    }

    public Boolean sendTransaction(double amount, String toAddress){
        if (balance >= amount) {
            Transaction transaction = new Transaction(amount, address, toAddress);
            broadcastTransaction(transaction);
            return true;
        }
        return false;
    }

    public static String randomAlphaNumeric(int count) {
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public void broadcastTransaction(Transaction transaction) {
        client.createNotification()
                .method("getTransactions")
                .param("transaction", transaction.toString())
                .execute();
    }

    JsonRpcClient client = new JsonRpcClient(new Transport() {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        @NotNull
        @Override
        public String pass(@NotNull String request) throws IOException {
            // Used Apache HttpClient 4.3.1 as an example
            HttpPost post = new HttpPost("http://json-rpc-server/team");
            post.setEntity(new StringEntity(request, Charsets.UTF_8));
            post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                return EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
            }
        }
    });
}
