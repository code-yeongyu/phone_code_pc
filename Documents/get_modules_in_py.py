import sys


def main():
    funcs = str(dir(__import__(sys.argv[1])))
    funcs = funcs.replace("\'", '\"').replace(',', ',\n')
    funcs = '{"'+sys.argv[1]+'": '+funcs+"}"
    file = open("function_lists"+sys.argv[1]+".json", 'w+')
    file.write(funcs)
    file.close()


main()
