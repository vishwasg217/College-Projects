import streamlit as st
import pandas as pd
import mysql.connector
from datetime import date
from dateutil.relativedelta import relativedelta

if st.session_state['loggedIn'] == True:

    st.header("Customer")

    db = mysql.connector.connect(
        host = "localhost",
        user = "root",
         passwd = "mysqlpassword123",
        database = "dbms"
    )
    cursor = db.cursor()

    age_filter = st.sidebar.slider("Enter an age range: ", 18, 50, (18, 50))
    date_end = date.today() - relativedelta(years=age_filter[0])
    date_start = date.today() - relativedelta(years=age_filter[1])

    cursor.execute('''
    call select_customers(%s, %s)
    ''', (date_start, date_end))
    
    df = pd.DataFrame(cursor.fetchall())
    try:
        df.columns = ['Customer ID', 'Customer Name', 'DOB', 'Gender', 'Phone Number', 'Points']
        cust_options = sorted(df['Customer Name'].unique())
        cust_select = st.sidebar.multiselect('Customers: ', cust_options, cust_options)
        df = df[df['Customer Name'].isin(cust_select)]
        if df.empty:
            st.error("Empty Set")
        else:
            st.write("Number of rows: ", len(df))
            st.write(df)
    except (ValueError, KeyError):
        st.error('Empty Set')
    
else:
    st.write("Please login to access this page")