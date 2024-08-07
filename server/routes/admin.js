const express = require("express");
const admin = require("../middlewares/admin");
const Staff = require("../models/staff");
const Account = require("../models/account");
const Subject = require("../models/subject");
const Tutor = require("../models/tutor");
const Class = require("../models/class");
const Payment = require("../models/payment");

const adminRouter = express.Router();

adminRouter.post("/admin/staff", admin, async (req, res) => {
  try {
    const account = await Account.findById(req.account);
    const {
      firstName,
      lastName,
      dateOfBirth,
      gender,
      contactNumber,
      email,
      portraitPhotos,
      taxId,
      address,
      qualification,
      position,
      citizenId,
      status,
    } = req.body;

    if (account.staffId) {
      let staff = await Staff.findById(account.staffId);
      staff.firstName = firstName;
      staff.lastName = lastName;
      staff.dateOfBirth = dateOfBirth;
      staff.gender = gender;
      staff.contactNumber = contactNumber;
      staff.email = email;
      staff.portraitPhotos = portraitPhotos;
      staff.taxId = taxId;
      staff.address = address;
      staff.qualification = qualification;
      staff.position = position;
      staff.citizenId = citizenId;
      staff.status = status;

      await staff.save();

      return res
        .status(200)
        .json({ message: "Staff updated successfully", staff });
    }

    const staffData = new Staff({
        firstName,
        lastName,
        dateOfBirth,
        gender,
        contactNumber,
        email,
        portraitPhotos,
        taxId,
        address,
        qualification,
        position,
        citizenId,
        status,
    });

    await staffData.save();

    const staffId = staffData._id;

    account.staffId = staffId;
    await account.save();

    res
      .status(201)
      .json({ message: "Staff created successfully", staff: staffData });
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ error: "Failed to create / update staff data" });
  }
});

adminRouter.get('/admin/staff/get', admin, async (req, res) => {
    try {
        const account = await Account.findById(req.account);
        
        const staff = await Staff.findById(account.staffId);
        if (!staff)
            return res.status(404).json({ message: 'Staff not found' });

        res.status(200).json(staff);
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to get staff data' });
    }
});

adminRouter.post('/admin/create-subject', admin, async (req, res) => {
    try {
        const { subjectName, grade } = req.body;

        const subject = await Subject.findOne({ subjectName, grade });
        if (subject)
            return res.status(409).json({ message: 'Subject already exists' }); 

        const newSubject = new Subject({ subjectName, grade });
        await newSubject.save();

        res.status(201).json({ message: 'Subject created successfully', newSubject });
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to create new subject' });
    }
});

adminRouter.patch('/admin/classes/:id/status', admin, async (req, res) => {
  try {
    const { status } = req.body;
        const validStatuses = ['approved', 'assigned', 'pending', 'rejected', 'completed'];

        if (!validStatuses.includes(status)) {
            return res.status(400).json({ error: 'Invalid status value' });
        }

        const updatedClass = await Class.findByIdAndUpdate(
            req.params.id,
            { status },
            { new: true, runValidators: true }
        );

        if (!updatedClass) {
            return res.status(404).json({ error: 'Class not found' });
        }

        res.status(200).json(updatedClass);
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
});


adminRouter.patch('/admin/tutors/:id/status', admin, async (req, res) => {
  try {
    const { status } = req.body;
    const validStatuses = ['rejected', 'pending', 'approved'];

    if (!validStatuses.includes(status)) {
      return res.status(400).json({ error: 'Invalid status value' });
    }

    const updatedTutor = await Tutor.findByIdAndUpdate(
      req.params.id,
      { status },
      { new: true, runValidators: true }
    ).populate('subjects'); 

    if (!updatedTutor) {
      return res.status(404).json({ error: 'Tutor not found' });
    }

    res.status(200).json(updatedTutor);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

adminRouter.get('/admin/revenue-statistics', admin, async (req, res) => {
  const { startDate, endDate } = req.query;

  try {
      const start = new Date(startDate);
      const end = new Date(endDate);

      const payments = await Payment.find({
          paymentDate: {
              $gte: start,
              $lte: end
          }
      }).populate('tutorId').populate('classId');

      if (payments.length === 0) {
          return res.json({ message: 'No payments found for the given period.' });
      }

      const revenueData = payments.map(payment => ({
          classId: payment.classId._id,
          tutorId: payment.tutorId._id,
          amount: parseFloat(payment.amount),
          paymentDate: payment.paymentDate
      }));

      res.json(revenueData);
  } catch (error) {
      console.error('Error fetching revenue statistics:', error);
      res.status(500).json({ error: 'Internal Server Error' });
  }
});

module.exports = adminRouter;
