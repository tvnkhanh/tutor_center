const express = require("express");
const { check, validationResult } = require('express-validator');
const bcryptjs = require("bcryptjs");
const PaymentInfo = require('../models/payment_info');
const auth = require('../middlewares/auth');

const paymentInfoRouter = express.Router();

paymentInfoRouter.post('/api/payment-info/create', [
    auth,
    check('cardNumber').isLength({ min: 16, max: 16 }).withMessage('Card number must be 16 digits'),
    check('validThru').matches(/^(0[1-9]|1[0-2])\/?([0-9]{2})$/).withMessage('Invalid expiry date'),
    check('cvv').isLength({ min: 3, max: 4 }).withMessage('CVV must be 3 or 4 digits'),
    check('cardHolder').notEmpty().withMessage('Card holder name is required'),
], async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
    }

    try {
        const { cardNumber, cardHolder, validThru, cvv, nation, tutorId, clientId, staffId } = req.body;
        
        const paymentInfo = await PaymentInfo.findOne({ cardNumber });

        if (paymentInfo) {
            return res.status(400).json({ error: 'Card number already exists' });
        }

        const newPaymentInfo = new PaymentInfo({
            cardNumber,
            cardHolder,
            validThru,
            cvv,
            nation,
            tutorId,
            clientId,
            staffId,
        });

        await newPaymentInfo.save();
        res.status(201).json(newPaymentInfo);
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to create payment info' });
    }
});

paymentInfoRouter.get('/api/payment-info/:cardNumber', auth, async (req, res) => {
    try {
        const { cardNumber } = req.params;

        const paymentInfo = await PaymentInfo.findOne({ cardNumber });
        
        if (!paymentInfo) {
            return res.status(404).json({ error: 'Payment info not found' });
        }

        res.status(200).json(paymentInfo);
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to get payment info' });
    }
});

paymentInfoRouter.post('/api/payment-info/delete/:cardNumber', auth, async (req, res) => {
    try {
        const { cardNumber } = req.params;

        const paymentInfo = await PaymentInfo.findOne({ cardNumber });

        if (!paymentInfo) {
            return res.status(404).json({ error: 'Payment info not found' });
        }
        if (paymentInfo.isDelete)
            return res.status(400).json({ message: 'Payment info is already deleted'});
        paymentInfo.isDelete = true;
        await paymentInfo.save();

        res.status(200).json({ message: 'Payment info deleted successfully' });
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to delete payment info' });
    }
});

module.exports = paymentInfoRouter;