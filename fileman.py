import os
import shutil


fileName1 = input("Enter the first file name: ")
fileName2 = input("Enter the second file name: ")

os.system(f"diff -U 0 firstv/{fileName1} secondv/{fileName2} > differences.txt")

os.makedirs('changefiles', exist_ok=True)

shutil.copy(f'firstv/{fileName1}', "firstv/original.java")

with open('differences.txt', 'r') as f:
    lines = f.readlines()

changeset_num = 0
changeset = []
file1 = ''
file2 = ''
isOpen = False
for line in lines:
    if line.startswith('@@'):
        changeset_num += 1

for line in lines:
    if line.startswith('---'):
        file1 = line
    if line.startswith('+++'):
        file2 = line
    if line.startswith('@@'):
        if isOpen:
           if changeset:  # If there's already a changeset, write it to a file
                if changeset_num > 10:
                    with open(f'changefiles/0{changeset_num - 1}.txt', 'w') as f:
                        f.write(''.join(changeset))
                else: 
                    with open(f'changefiles/00{changeset_num - 1}.txt', 'w') as f:
                        f.write(''.join(changeset))
                changeset_num -= 1
                changeset = []
                isOpen = False 
        if not isOpen:
            changeset.append(file1)
            changeset.append(file2)
            changeset.append(line)
            isOpen = True
            
    if (line.startswith('+') or line.startswith('-')) and not (line.startswith('+++') or line.startswith('---')):
        changeset.append(line)

# Don't forget the last changeset
if changeset:
    with open(f'changefiles/00{changeset_num - 1}.txt', 'w') as f:
        f.write(''.join(changeset))
