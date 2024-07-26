const express = require('express');
const Payment = require('../models/payment');
const auth = require('../middlewares/auth');

const paymentRouter = express.Router();

paymentRouter.post('/api/make-payment', auth, async (req, res) => {
    try {
        const { tutorId, clientId, amount, currency, paymentMethodId, paymentDate, classId } = req.body;

        const validCurrencies = ['usd', 'vnd'];
        if (!validCurrencies.includes(currency)) {
            return res.status(400).json({ error: 'Invalid currency' });
        }

        let amountInSmallestUnit;
        if (currency === 'usd') {
            amountInSmallestUnit = parseFloat(amount) * 100; 
        } else if (currency === 'vnd') {
            amountInSmallestUnit = parseFloat(amount); 
        } 

        const paymentIntent = await stripe.paymentIntents.create({
            amount: amountInSmallestUnit,
            currency: currency,
            payment_method: paymentMethodId,
            confirm: true,
        });

        const newPayment = new Payment({
            tutorId,
            clientId,
            amount,
            currency,
            paymentMethodId,
            paymentDate,
            classId
        });

        const savedPayment = await newPayment.save();

        res.status(201).json({
            paymentIntent,
            payment: savedPayment
        });
    } catch (e) {
        res.status(500).json({ error: 'Client failed to make payment' });
    }
});

module.exports = paymentRouter;