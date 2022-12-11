package com.lcyanxi.manage;

import com.alibaba.fastjson.JSON;
import com.lcyanxi.model.ShowLine;
import org.assertj.core.util.Lists;

import java.io.IOException;
import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/02/24/4:26 下午
 */
public class JsonTest {
    public static void main(String[] args) throws IOException {
//        String json1 =
//                "{\"action_definition\":[{\"item\":[{\"effective_time\":\"\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\"trend\",\"unit\":\"\",\"value\":\"\",\"trendList\":[\"-\",\"+\"],\"intra_relation\":\"and\",\"voice_content\":\"\",\"voice_url\":\"\",\"error_message\":\"双腿角度变小变大算一次\",\"priority\":\"\",\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"length\",\"point_list\":[0,1],\"sign\":\"trend\",\"trendList\":[\"+\"],\"unit\":\"\",\"value\":\"\"}},{\"effective_time\":\"-\",\"parameter_type\":\"angle\",\"point_list\":[12,13,14],\"sign\":\"<\",\"unit\":\"degree\",\"value\":120,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"\",\"voice_url\":\"\",\"error_message\":\"顶点时，膝盖角度不能太大\",\"priority\":\"\",\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"coordinateY\",\"point_list\":[9],\"sign\":\">\",\"trendList\":[],\"unit\":\"cm\",\"value\":0}},{\"effective_time\":\"+\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\">\",\"unit\":\"degree\",\"value\":150,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"\",\"voice_url\":\"\",\"error_message\":\"收脚时，腿部站直\",\"priority\":\"\",\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}}],\"inter_relation\":\"and\"}],\"action_standard\":[{\"item\":[{\"effective_time\":\"-\",\"parameter_type\":\"two_lines_angle\",\"point_list\":[9,11,-1],\"sign\":\"<\",\"unit\":\"degree\",\"value\":20,\"trendList\":[],\"intra_relation\":\"or\",\"voice_content\":\"前腿大腿应蹲至与地面平行\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770747193_.mp3\",\"error_message\":\"下蹲时，大腿与地面平行\",\"priority\":1,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}},{\"effective_time\":\"-\",\"parameter_type\":\"two_lines_angle\",\"point_list\":[10,12,-1],\"sign\":\"<\",\"unit\":\"degree\",\"value\":20,\"trendList\":[],\"intra_relation\":\"or\",\"voice_content\":\"前腿大腿应蹲至与地面平行\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770747193_.mp3\",\"error_message\":\"下蹲时，大腿与地面平行\",\"priority\":1,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"coordinateY\",\"point_list\":[9],\"sign\":\">\",\"trendList\":[],\"unit\":\"cm\",\"value\":0}}],\"inter_relation\":\"and\"},{\"item\":[{\"effective_time\":\"-\",\"parameter_type\":\"angle\",\"point_list\":[11,13,9],\"sign\":\"<\",\"unit\":\"degree\",\"value\":100,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"下蹲时，后腿膝关节夹角应接近 90 度\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770798503_.mp3\",\"error_message\":\"两腿膝盖都要在 90 度左右\",\"priority\":2,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}},{\"effective_time\":\"-\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\"<\",\"unit\":\"degree\",\"value\":100,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"下蹲时，后腿膝关节夹角应接近 90 度\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770798503_.mp3\",\"error_message\":\"两腿膝盖都要在 90 度左右\",\"priority\":2,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"coordinateY\",\"point_list\":[9],\"sign\":\">\",\"trendList\":[],\"unit\":\"cm\",\"value\":0}}],\"inter_relation\":\"and\"},{\"item\":[{\"effective_time\":\"whole_process\",\"parameter_type\":\"two_lines_angle\",\"point_list\":[1,2,-2],\"sign\":\"<\",\"unit\":\"degree\",\"value\":20,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"上身保持直立\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770758509_.mp3\",\"error_message\":\"全程腰背挺直\",\"priority\":3,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}}],\"inter_relation\":\"and\"}]}\t";
//        String json2 =
//                "{\"action_definition\":[{\"item\":[{\"effective_time\":\"\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\"trend\",\"unit\":\"\",\"value\":\"\",\"trendList\":[\"-\",\"+\"],\"intra_relation\":\"and\",\"voice_content\":\"\",\"voice_url\":\"\",\"error_message\":\"双腿角度变小变大算一次\",\"priority\":\"\",\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"length\",\"point_list\":[0,1],\"sign\":\"trend\",\"trendList\":[\"+\"],\"unit\":\"\",\"value\":\"\"}},{\"effective_time\":\"-\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\"<\",\"unit\":\"degree\",\"value\":120,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"\",\"voice_url\":\"\",\"error_message\":\"顶点时，膝盖角度不能太大\",\"priority\":\"\",\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"coordinateY\",\"point_list\":[9],\"sign\":\">\",\"trendList\":[],\"unit\":\"cm\",\"value\":0}},{\"effective_time\":\"+\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\">\",\"unit\":\"degree\",\"value\":150,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"\",\"voice_url\":\"\",\"error_message\":\"收脚时，腿部站直\",\"priority\":\"\",\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}}],\"inter_relation\":\"and\"}],\"action_standard\":[{\"item\":[{\"effective_time\":\"-\",\"parameter_type\":\"two_lines_angle\",\"point_list\":[9,11,-1],\"sign\":\"<\",\"unit\":\"degree\",\"value\":20,\"trendList\":[],\"intra_relation\":\"or\",\"voice_content\":\"前腿大腿应蹲至与地面平行\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770747193_.mp3\",\"error_message\":\"下蹲时，大腿与地面平行\",\"priority\":1,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}},{\"effective_time\":\"-\",\"parameter_type\":\"two_lines_angle\",\"point_list\":[10,12,-1],\"sign\":\"<\",\"unit\":\"degree\",\"value\":20,\"trendList\":[],\"intra_relation\":\"or\",\"voice_content\":\"前腿大腿应蹲至与地面平行\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770747193_.mp3\",\"error_message\":\"下蹲时，大腿与地面平行\",\"priority\":1,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"coordinateY\",\"point_list\":[9],\"sign\":\">\",\"trendList\":[],\"unit\":\"cm\",\"value\":0}}],\"inter_relation\":\"and\"},{\"item\":[{\"effective_time\":\"-\",\"parameter_type\":\"angle\",\"point_list\":[11,13,9],\"sign\":\"<\",\"unit\":\"degree\",\"value\":100,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"下蹲时，后腿膝关节夹角应接近 90 度\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770798503_.mp3\",\"error_message\":\"两腿膝盖都要在 90 度左右\",\"priority\":2,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}},{\"effective_time\":\"-\",\"parameter_type\":\"angle\",\"point_list\":[12,10,14],\"sign\":\"<\",\"unit\":\"degree\",\"value\":100,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"下蹲时，后腿膝关节夹角应接近 90 度\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770798503_.mp3\",\"error_message\":\"两腿膝盖都要在 90 度左右\",\"priority\":2,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"coordinateY\",\"point_list\":[9],\"sign\":\">\",\"trendList\":[],\"unit\":\"cm\",\"value\":0}}],\"inter_relation\":\"and\"},{\"item\":[{\"effective_time\":\"whole_process\",\"parameter_type\":\"two_lines_angle\",\"point_list\":[1,2,-2],\"sign\":\"<\",\"unit\":\"degree\",\"value\":20,\"trendList\":[],\"intra_relation\":\"and\",\"voice_content\":\"上身保持直立\",\"voice_url\":\"https://static1.keepcdn.com/ark_cms-kit-nirvana/2022/12/29/17/39/1640770758509_.mp3\",\"error_message\":\"bbbbbbbb\",\"priority\":3,\"sub\":{\"effective_time\":\"\",\"parameter_type\":\"\",\"point_list\":[],\"sign\":\"\",\"trendList\":[],\"unit\":\"\",\"value\":\"\"}}],\"inter_relation\":\"and\"}]}\t";
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        TypeReference<HashMap<String, Object>> type = new TypeReference<HashMap<String, Object>>() {};
//
//
//        HashMap<String, Object> j1 = mapper.readValue(json1, type);
//        HashMap<String, Object> j2 = mapper.readValue(json2, type);
//
//        Map<String, Object> flatten1 = FlatMapUtil.flatten(j1);
//        Map<String, Object> flatten2 = FlatMapUtil.flatten(j2);
//        MapDifference<String, Object> difference = Maps.difference(flatten1, flatten2);
//        Map<String, MapDifference.ValueDifference<Object>> differenceMap = difference.entriesDiffering();
//
//        System.out.println(difference.entriesDiffering().values());
//
//        String aa = "OperateLogAnnotation";
//        System.out.println(aa.toLowerCase(Locale.ROOT));
        List<String> types = Lists.newArrayList("beats_boxing","dance_pad","dance_master");
        List<ShowLine> showLineList = Lists.newArrayList();

        for (String str : types){
            ShowLine build = ShowLine.builder().metaType(str).build();
            List<ShowLine.LineInfo> list = Lists.newArrayList();
            for (int i =0 ; i< 2; i++){
                ShowLine.LineInfo type = ShowLine.LineInfo.builder().id(i).showType(Lists.newArrayList("11", "22")).build();
                list.add(type);
            }
            build.setInfos(list);
            showLineList.add(build);
        }

        System.out.println(JSON.toJSONString(showLineList));
    }


}
