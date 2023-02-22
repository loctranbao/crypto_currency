public class TxHandler {

    UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        UTXOPool usedUtxo = new UTXOPool();
        double coinConsumed  = 0.0;
        double coinCreated = 0.0;

        for(int i = 0; i < tx.numInputs(); ++i) {
            Transaction.Input currentInput = tx.getInput(i);
            UTXO utxo = new UTXO(currentInput.prevTxHash, currentInput.outputIndex);

            if(!this.utxoPool.contains(utxo)) {
                return false;
            }

            //double spending
            if(usedUtxo.contains(utxo)) {
                return false;
            }

            Transaction.Output output = this.utxoPool.getTxOutput(utxo);
            if(!Crypto.verifySignature(output.address, tx.getRawDataToSign(i), currentInput.signature)) {
                return false;
            }

            coinConsumed += output.value;
            usedUtxo.addUTXO(utxo, output);
        }


        for(Transaction.Output output : tx.getOutputs()) {
            if(output.value < 0) {
                return false;
            }
            coinCreated += output.value;
        }

        return coinConsumed >= coinCreated;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        return null;
    }

}
