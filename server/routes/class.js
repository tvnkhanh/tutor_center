const express = require("express");
const Class = require("../models/class");
const auth = require("../middlewares/auth");

const classRouter = express.Router();

classRouter.get("/api/classes", auth, async (req, res) => {
  try {
    const classes = await Class.find();
    res.status(200).json(classes);
  } catch (e) {
    res.status(500).json({ error: "Failed to get classes data" });
  }
});

classRouter.post("/api/create-class", auth, async (req, res) => {
  try {
    const {
      studentInfo,
      schedule,
      status,
      salary,
      clientId,
      tutorId,
      form,
      requirement,
      address,
      subjectIds,
    } = req.body;

    const subjects = await Subject.find({ _id: { $in: subjectIds } });
    if (subjects.length !== subjectIds.length) {
      return res.status(400).json({ message: "Some subjects were not found" });
    }

    const newClass = new Class({
      studentInfo: studentInfo,
      schedule: schedule,
      status: status,
      salary: salary,
      tutorId: tutorId,
      clientId: clientId,
      form: form,
      requirement: requirement,
      address: address,
      subjects: subjectIds
    });

    const savedClass = await newClass.save();
    res.status(201).json({ message: "Tutor created successfully", class: savedClass, subjects });
  } catch (e) {
    res.status(500).json({ error: "Failed to get classes data" });
  }
});

module.exports = classRouter;
