import{Z as d,d as h,H as v,i as w,c as s,F as y,r as f,k as b,a as n,l as k,w as x,g as C,a9 as I,y as S,G as E}from"./index-086108b4.js";import{_ as $}from"./InputElement.vue_vue_type_script_setup_true_lang-b901796a.js";function T(o){function r(){setTimeout(()=>{if(o.value){const t=document.querySelector(o.value);t&&t.scrollIntoView({behavior:"smooth"})}})}d(o,r,{immediate:!0})}const q=h({__name:"InputGroup",props:{fields:null,modelValue:null,columnCount:{default:1},validationErrors:{default:void 0}},emits:["update:modelValue"],setup(o,{emit:r}){const t=o,m="2rem",_={gap:m},V=v(()=>({width:`calc((100% - ${m} * ${t.columnCount-1}) / ${t.columnCount})`})),g=v(()=>{const{fields:i,columnCount:c}=t,u=[];for(let a=0;a<i.length;a+=c){const e=i.slice(a,a+c);u.push(e)}return u}),l=w({});return d(()=>t.modelValue,()=>l.value=t.modelValue?t.modelValue:{},{immediate:!0,deep:!0}),d(l,()=>{r("update:modelValue",l.value)},{deep:!0}),(i,c)=>(n(),s("div",{class:"input-group pb-[2rem]",style:_},[(n(!0),s(y,null,f(b(g),(u,a)=>(n(),s("div",{key:a,class:"input-group__row",style:_},[(n(!0),s(y,null,f(u,e=>(n(),k(S,{id:e.name,key:e.name,class:"input-group__row__field",label:e.label,"label-position":e.inputAttributes.labelPosition,required:e.required,style:I(b(V))},{default:x(()=>[C($,{id:e.name,modelValue:l.value[e.name],"onUpdate:modelValue":p=>l.value[e.name]=p,attributes:e.inputAttributes,type:e.type,"validation-error":t.validationErrors&&t.validationErrors.find(p=>p.field.split(".")[1]===e.name)},null,8,["id","modelValue","onUpdate:modelValue","attributes","type","validation-error"])]),_:2},1032,["id","label","label-position","required","style"]))),128))]))),128))]))}});const z=E(q,[["__scopeId","data-v-94aacb5f"]]);export{z as I,T as u};