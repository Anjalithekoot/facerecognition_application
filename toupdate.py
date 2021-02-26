import pandas as pd
import config
import numpy as np
import os
import datetime
import json

import flask
import werkzeug
from flask import request
#from werkzeug.contrib.fixers import ProxyFix

app = flask.Flask(__name__)

@app.route('/postjson', methods = ['POST'])
def postJsonHandler():
    
    content = request.get_json()
    print('entered to update')

    data = content['x']
    df = pd.read_csv("attendance/attendance.csv")
    for name in data:
        
        df.loc[df.Names==name,df.columns[-1]] = 1
        df.to_csv("attendance/attendance.csv",index=False)

    json_string = json.dumps(data)
    return json_string

from werkzeug.middleware.proxy_fix import ProxyFix
#app.wsgi_app = ProxyFix(app.wsgi_app, x_proto=1, x_host=1)
#app.wsgi_app = ProxyFix(app.wsgi_app)
app.run(host="0.0.0.0", port="5001", debug=True)

