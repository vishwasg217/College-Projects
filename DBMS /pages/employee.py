import streamlit as st
import pandas as pd
import mysql.connector
from datetime import date
from dateutil.relativedelta import relativedelta

if st.session_state['loggedIn'] == True:

    st.header("Employees")

    db = mysql.connector.connect(
        host = "localhost",
        user = "root",
        passwd = "mysqlpassword123",
        database = "dbms"
    )
    cursor = db.cursor()

    age_filter = st.sidebar.slider("Enter an age range: ", 18, 50, (18, 50))
    salary_filter = st.sidebar.slider("Choose salary range: ", 10000, 50000, (10000, 50000))

    date_end = date.today() - relativedelta(years=age_filter[0])
    date_start = date.today() - relativedelta(years=age_filter[1])

    cursor.execute('''
    call select_employees(%s, %s, %s, %s)
    ''', (date_start, date_end, salary_filter[0], salary_filter[1]))

    df = pd.DataFrame(cursor.fetchall())
    try:
        df.columns = ['Employee ID', 'Employee Name', 'DOB', 'Gender', 'Phone Number', 'Address', 'Salary', 'Job']
        gender_options = sorted(df['Gender'].unique())
        gender_select = st.sidebar.multiselect("Gender: ", gender_options, gender_options)

        emp_name = sorted(df['Employee Name'].unique())
        emp_select = st.sidebar.multiselect("Name: ", emp_name, emp_name)

        df = df[(df['Gender'].isin(gender_select)) & (df['Employee Name'].isin(emp_select))]
        if df.empty:
            st.error("Empty Set")
        else:
            st.write("Number of rows: ", len(df))
            st.write(df)
    except (ValueError, KeyError):
        st.error("Empty Set")
    
else:
    st.write("Plase login to access this page")