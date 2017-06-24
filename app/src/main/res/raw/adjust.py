import os
import json

data = []
with open('exported_data_original.ldjson', 'r') as fr:
	with open('exported_data.ldjson', 'w') as fw:
		for line in fr:
			obj = json.loads(line)
			obj_new = {}
			obj_new['annotatorA'] = obj['annotatorA']
			obj_new['annotatorB'] = obj['annotatorB']
			obj_new['annotatorC'] = obj['annotatorC']
			obj_new['retweet_count'] = obj['retweet_count']
			obj_new['id'] = int(str(obj['id'])[:6])
			obj_new['majority'] = obj['majority']
			fw.write(json.dumps(obj_new)+'\n')
