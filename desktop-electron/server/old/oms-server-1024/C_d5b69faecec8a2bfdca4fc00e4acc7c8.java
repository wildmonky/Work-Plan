import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_d5b69faecec8a2bfdca4fc00e4acc7c8
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('ZWDKXWSB_JXWKG','"+mapRow.get("sq_ID")+"');";
return url;

    }
}
