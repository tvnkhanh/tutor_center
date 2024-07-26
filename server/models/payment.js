const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const paymentSchema = new Schema({
    tutorId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Tutor' 
    },
    clientId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Client' 
    },
    amount: {
        type: String,
        required: true,
        min: 0
    },
    paymentMethodId: {
        type: Schema.Types.ObjectId,
        ref: 'PaymentMethod'
    },
    paymentDate: {
        type: Date,
        required: true
    },
    classId: {
        type: Schema.Types.ObjectId,
        ref: 'Class'
    }
})

const Payment = mongoose.model('Payment', paymentSchema);
module.exports = Payment;