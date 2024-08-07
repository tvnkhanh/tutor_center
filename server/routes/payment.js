const express = require('express');
const Payment = require('../models/payment');
const auth = require('../middlewares/auth');
const Class = require('../models/class');
const PaymentMethod = require('../models/payment_method');

const paymentRouter = express.Router();

// Endpoint to make a payment
paymentRouter.post('/api/make-payment', auth, async (req, res) => {
    const { tutorId, amount, paymentMethodId, paymentDate, classId } = req.body;

    try {
        const newPayment = new Payment({
            tutorId,
            amount,
            paymentMethodId,
            paymentDate,
            classId
        });

        const savedPayment = await newPayment.save();
        res.status(201).json(savedPayment);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Endpoint to get payment information for a tutor
paymentRouter.get('/api/get-payment/tutor', auth, async (req, res) => {
    const { tutorId, classId } = req.query;

    if (!tutorId || !classId) {
        return res.status(400).json({ message: 'Missing tutorId or classId' });
    }

    try {
        const payments = await Payment.find({ tutorId, classId });
        res.json(payments);
    } catch (error) {
        res.status  .json({ message: error.message });
    }
});

// Endpoint to get payment info for a tutor
paymentRouter.get('/api/get-payment-info/tutor', auth, async (req, res) => {
    try {
        const classes = await Class.find({
            tutorId: req.query.tutorId,
            status: { $in: ['assigned', 'completed'] }
        }).populate({
            path: 'tutorId',
            populate: { path: 'subjects' }
        }).populate('clientId').populate('subjects');

        const result = await Promise.all(classes.map(async (classItem) => {
            const payment = await Payment.findOne({
                tutorId: req.query.tutorId,
                classId: classItem._id
            });

            return {
                ...classItem.toObject(),
                paymentStatus: payment ? 'paid' : 'unpaid',
                payment: payment ? payment.toObject() : null
            };
        }));

        res.json(result);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Endpoint to create a payment method
paymentRouter.post('/api/create-payment-method', auth, async (req, res) => {
    try {
        const { method } = req.body;

        const newPaymentMethod = new PaymentMethod({
            method
        });

        const savedPaymentMethod = await newPaymentMethod.save();
        res.status(201).json(savedPaymentMethod);
    } catch (e) {
        res.status(500).json({ message: e.message });
    }
});

module.exports = paymentRouter;
