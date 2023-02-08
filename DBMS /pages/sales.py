import streamlit as st
import pandas as pd
import mysql.connector
import datetime

if st.session_state['loggedIn'] == True:
    st.header("Sales")

    db = mysql.connector.connect(
        host = "localhost",
        user = "root",
        passwd = "mysqlpassword123",
        database = "dbms"
    )
    cursor = db.cursor()

    # date filter
    ds = st.sidebar.date_input("Start Date: ", datetime.date(2013, 1, 1))
    de = st.sidebar.date_input("End Date: ", datetime.date(2025, 1, 1))

    cursor.execute(
    ''' call select_sales(%s, %s)'''
    , (ds, de))

    df = pd.DataFrame(cursor.fetchall())

    try:
        df.columns = ['Bill ID', 'Customer Name', 'Product Name', 'Quantity', 'Bill Amount', 'Billing Date']
        #product selection
        product_options = sorted(df['Product Name'].unique())
        product_select = st.sidebar.multiselect('Product', product_options, product_options)

        # Customer selection
        customer_options = sorted(df['Customer Name'].unique())
        customer_select = st.sidebar.multiselect('Customer', customer_options, customer_options)

        # applying filter
        df = df[(df['Product Name'].isin(product_select)) & (df['Customer Name'].isin(customer_select))]
        if df.empty:
            st.write("Empty Set")
        else:
            st.write("Number of rows: ", len(df))
            st.write(df)
    except (ValueError, KeyError):
        st.write("Empty Set")
    
else:
    st.write("Please login to access this page")