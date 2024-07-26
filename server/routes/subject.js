const express = require('express');
const Subject = require('../models/subject');
const auth = require('../middlewares/auth');

const subjectRouter = express.Router();

subjectRouter.get('/api/subjects', auth, async (req, res) => {
    try {
        const subjects = await Subject.find();
        res.status(200).json(subjects);
    } catch (err) {
        res.status(500).send(err);
    }
});

module.exports = subjectRouter;