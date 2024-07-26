const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const invoiceSchema = new Schema({
    invoiceName: {
        type: String,
        required: true
    },
    createdDate: {
        type: Date,
        default: Date.now
    },
    paymentId: {
        type: Schema.Types.ObjectId,
        ref: 'Payment'
    },
    staffId: {
        type: Schema.Types.ObjectId,
        ref: 'Staff'
    }
});

const Invoice = mongoose.model('Invoice', invoiceSchema);
module.exports = Invoice;