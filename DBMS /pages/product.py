import datetime
import streamlit as st
import pandas as pd
import mysql.connector

if st.session_state['loggedIn'] == True:

    st.header("Product")

    db = mysql.connector.connect(
        host = "localhost",
        user = "root",
        passwd = "mysqlpassword123",
        database = "dbms"
    )
    cursor = db.cursor()

    # price filter
    price_filter = st.sidebar.slider('Select a price range: ', 0, 300, (0, 300))
    # stock filter 
    stock_filter = st.sidebar.slider('Select a range for number of stock: ', 0, 100, (0, 100))

    cursor.execute(
    '''call select_product(%s, %s, %s, %s) '''
    , (price_filter[0], price_filter[1], stock_filter[0], stock_filter[1]))

    df = pd.DataFrame(cursor.fetchall())
    try:
        df.columns = ['Product ID', 'Product Name', 'Category Name', 'Price', 'Stock']
        product_options = sorted(df['Product Name'].unique())
        product_select = st.sidebar.multiselect('Products: ', product_options, product_options)

        category_options = sorted(df['Category Name'].unique())
        category_select = st.sidebar._multiselect('Category: ', category_options, category_options)

        # apply filters
        df = df[(df['Product Name'].isin(product_select)) & (df['Category Name'].isin(category_select))]
        if df.empty:
            st.error("Empty Set")
        else:
            st.write("Number of rows: ", len(df))
            st.write(df)
    except ValueError:
        st.error("Empty Set")

else:
    st.write("Please login to access this page")