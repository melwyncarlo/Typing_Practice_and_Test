import json

with open('paragraphs.json', 'r') as fileReader:
   paragraphs = json.load(fileReader)

numberOfParagraphs = 0

for paragraph in paragraphs['data']:
   numberOfParagraphs += 1
   fileWriter = open('../TypingPracticeAndTest/data/paragraphs/' + str(numberOfParagraphs), 'w')
   fileWriter.write(paragraph['paragraph'] + '\n')
   fileWriter.close()

print('\n ' + str(numberOfParagraphs) + ' paragraphs parsed!\n')

