// 设置异步间隔延迟
export function delay(interval = 0)
{
    return new Promise(resolve => {
        let timer = setTimeout(_ =>{
            clearTimeout(timer);
            resolve();
        },interval);
    });
}

// 把文件按照二进制读取
// export function readFile(file)
// {
//     return new Promise(resolve => {
//         let reader = new FileReader();
//         reader.readAsBinaryString(file);
//         reader.onload=ev=>{
//             resolve(ev.target.result);
//         };
//     });
// }