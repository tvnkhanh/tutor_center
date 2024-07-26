const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const Schema = mongoose.Schema;

const paymentInfoSchema = new mongoose.Schema({
    cardNumber: {
        type: String,
        required: true,
        unique: true,
    },
    cardHolder: {
        type: String,
        required: true,
    },
    validThru: {
        type: String,
        required: true,
    },
    cvv: {
        type: String,
        required: true,
    },
    nation: {
        type: String,
        required: true,
    },
    tutorId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Tutor' 
    },
    clientId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Client' 
    },
    staffId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Staff'
    },
    isDelete: {
        type: Boolean,
        default: false,
    }
});

paymentInfoSchema.pre('save', async function (next) {
    try {
        const salt = await bcrypt.genSalt(10);

        if (this.isModified('cvv')) {
            this.cvv = await bcrypt.hash(this.cvv, salt);
        }

        next();
    } catch (err) {
        next(err);
    }
});

const PaymentInfo = mongoose.model('PaymentInfo', paymentInfoSchema);
module.exports = PaymentInfo;
