require("dotenv").config();

const express = require("express");
const mongoose = require("mongoose");

const authRouter = require('./routes/auth');
const adminRouter = require('./routes/admin');
const paymentInfoRouter = require('./routes/payment_info');
const clientRouter = require('./routes/client');
const tutorRouter = require('./routes/tutor');
const subjectRouter = require('./routes/subject');
const classRouter = require('./routes/class');

const app = express();
const PORT = 3000;
const DB = process.env.DB_CONNECTION_STRING;

// middleware
app.use(express.json());
app.use(authRouter);
app.use(adminRouter);
app.use(paymentInfoRouter);
app.use(clientRouter);
app.use(tutorRouter);
app.use(subjectRouter);
app.use(classRouter);

mongoose
  .connect(DB)
  .then(() => {
    console.log("connection successful");
  })
  .catch((e) => {
    console.log(e);
  });

app.listen(PORT, "0.0.0.0", () => {
  console.log(`connected at port ${PORT}`);
});
